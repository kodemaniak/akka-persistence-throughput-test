package test

import akka.actor.Actor
import akka.actor.Props
import java.util.UUID
import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.persistence.Persistent
import com.typesafe.config.ConfigFactory

/**
 * @author carsten
 *
 */
class Sender(numIds: Int, msgsPerId: Int, region: ActorRef, createData: String => AnyRef) extends Actor {

  private val ids: Seq[String] = (1 to numIds) map (_ => UUID.randomUUID().toString)
  private var remaining = ids
  private val upper: Int = numIds * msgsPerId //50000
  private val lower: Int = 0 //1000
  private var pending: Int = 0

  var start = System.currentTimeMillis()
  send()

  def receive = benchmarking(finishedSendingNotInitialized, 1)
  
  def benchmarking(nextState: Receive, iterations: Int): Receive = {
    case DomainEventReceived(_) =>
      pending -= 1
      send()
      if (remaining.isEmpty && iterations == 1) context become nextState
      else if (remaining.isEmpty) {
        remaining = ids
        send()
        context become benchmarking(nextState, iterations - 1)
      }
  }

  def finishedSendingNotInitialized: Receive = {
    case DomainEventReceived(_) =>
      pending -= 1
      if (pending == 0) {
        println("finished uninitialized")
        val end = System.currentTimeMillis()
        val time = (end - start)
        val throughput = numIds.toDouble / time
        println(s"time: ${time / 1000} s")
        println(s"${throughput * 1000} msgs/s")
        remaining = ids
        start = System.currentTimeMillis()
        context become benchmarking(finishedSendingThroughput, msgsPerId)
        send()
      }
  }

  def finishedSendingThroughput: Receive = {
    case DomainEventReceived(_) =>
      pending -= 1
      if (pending == 0) {
        println("finished initialized")
        val end = System.currentTimeMillis()
        val time = (end - start)
        val throughput = numIds.toDouble * msgsPerId / time
        println(s"time: ${time / 1000} s")
        println(s"${throughput * 1000} msgs/s")
        context.system.shutdown()
      }
  }

  private def send(): Boolean = {
    if (pending <= lower) {
      val toSend = remaining.take(upper - pending)
      remaining = remaining.drop(upper - pending)
      println(s"${remaining.size} remaining")
      toSend.par foreach (id => region ! createData(id))
      pending += toSend.size
      true
    } else false
  }
}
