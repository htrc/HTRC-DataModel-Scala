package org.hathitrust.htrc.data.exceptions

case class MalformedVolumeException(msg: String, cause: Throwable = null) extends Exception(msg, cause)