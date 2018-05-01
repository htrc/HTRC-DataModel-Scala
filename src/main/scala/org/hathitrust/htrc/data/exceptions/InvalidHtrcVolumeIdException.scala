package org.hathitrust.htrc.data.exceptions

@SuppressWarnings(Array("org.wartremover.warts.Null"))
case class InvalidHtrcVolumeIdException(id: String, message: String = null, cause: Throwable = null)
  extends Exception(s"$id" + Option(message).map(msg => s": $msg").getOrElse(""), cause)
