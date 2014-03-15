package test

import akka.actor.Actor
import akka.actor.ActorLogging

/**
 * @author carsten
 *
 */
class NonPersistentReceiver extends Actor with ActorLogging {
  private var lastPayload: Option[String] = None
  
	def receive = {
	  case DomainEvent(id, payload) => 
	    lastPayload = Some(payload)
	    sender ! DomainEventReceived(id)
	}
}