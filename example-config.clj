;Config file for clspam default values specified as comments
{
  ;where to listen for incoming lmtp connections
  ;:listen-port 10024
  ;:listen-address nil

  ;Where to send messages after filtering
  ;:lmtp-port nil
  ;:lmtp-address "127.0.0.1"

  ;How to break the message into initial tokens
  ;common examples:
  ;[[:alnum:]]+
  ;[-.,:[:alnum:]]+
  ;default is all printable characters
  ;:token-regex "[[:graph:]]+"

  ;A class that transforms the message into the actual hash values that will be stored
  ;must implement clspam.protocol.hasher
  ;:token-hasher nil

  ;A class that scores the message based on the values from the :token-hasher
  ;implements clspam.protocol.scorer
  ;:scoring-algorithm nil

  ;A string that separates the email address user from the extension
  ;e.g.: user+extension@domain the separator is "+"
  ;:address-extension-separator nil

  ;Where to store token information
  ;:token-file nil
}