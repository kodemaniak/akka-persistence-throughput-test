package test

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import akka.persistence.Persistent

/**
 * @author carsten
 *
 */
class Region(props: Props) extends Actor {
	def receive = {
	  case ev @ DomainEvent(id, _) => 
	    receiverById(id) forward ev
//	    sender ! DomainEventReceived(id)
	  case ev @ Persistent(DomainEvent(id, _), _) => 
	    receiverById(id) forward ev
//	    sender ! DomainEventReceived(id)
	}
	
	private def receiverById(id: String): ActorRef = context.child(id) getOrElse newChild(id)
	private def newChild(id: String): ActorRef = context.actorOf(props.withDispatcher("receiver-dispatcher"), id)
}