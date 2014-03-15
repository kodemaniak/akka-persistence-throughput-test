package test

import akka.actor.Actor
import akka.actor.Props
import java.util.UUID
import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.persistence.Persistent
import com.typesafe.config.ConfigFactory

/**
 * First argument defines whether a persistent receiver or a non persistent receiver should be used. Use "p" for a persistent one.
 * Second argument defines number of receivers. All receivers are managed by a region actor, simulating a sharded environment.
 * Third argument defines how many iterations to run for the throughput test.
 * 
 * First each receiver is send a single message to benchmark recovery throughput, i.e., how many messages per second can be handled
 * when receivers have to be recovered.
 * Second test iterates several times and sends messages to already existing receivers, benchmark write throughput.
 * 
 * @author carsten
 *
 */
object Main extends App {
  val persistent = args(0) == "p"
  val numIds = args(1).toInt
  val msgsPerId = args(2).toInt

  val system = ActorSystem("throughput-test")
  
  val createData: String => AnyRef = id => if (persistent) Persistent(DomainEvent(id, id)) else DomainEvent(id, id)
  
  private val receiverProps = if (!persistent) Props(classOf[NonPersistentReceiver]) else Props(classOf[CommandSourcingReceiver])
//  private val region = system.actorOf(Props(classOf[Region], receiverProps).withDispatcher("region-dispatcher"), "region")
  private val region = system.actorOf(Props(classOf[CommandSourcingReceiver]).withDispatcher("receiver-dispatcher"), "region")
  system.actorOf(Props(classOf[Sender], numIds, msgsPerId, region, createData), "sender")
  
  system.awaitTermination()
}
