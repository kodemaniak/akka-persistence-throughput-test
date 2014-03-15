package test

import akka.actor.Actor
import akka.persistence.Processor
import akka.persistence.Persistent

/**
 * @author carsten
 *
 */
class CommandSourcingReceiver extends Processor {
  private var lastPayload: Option[String] = None
  
	def receive = {
	  case Persistent(DomainEvent(id, payload), _) =>
	    lastPayload = Some(payload)
	    if (recoveryFinished)
	    	sender ! DomainEventReceived(id)
	}
}