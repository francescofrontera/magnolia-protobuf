package io.github.francescofrontera

private[francescofrontera] object Label {
  sealed trait Arity {
    def label: String
  }

  case object Required extends Arity {
    override def label: String = "required"
  }

  case object Optional extends Arity {
    override def label: String = "optional"
  }
}
