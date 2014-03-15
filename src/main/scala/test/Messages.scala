package test

case class DomainEvent(id: String, payload: String)
case class DomainEventReceived(id: String)
