(ns bankd.test.command-bus
  (:use bankd.support.command-bus :reload-all
        midje.sweet))

(facts "execute-command"
  (fact "executes the command and returns the result"
    (execute-command .cmd. .arg.) => .result.
    (provided
      (.cmd. .arg.) => .result.))
  (fact "saves and publishes any created events"
    (execute-command .cmd. .arg.) => .result.
    (provided
      (.cmd. .arg.) => .result.
      (commit-repository!) => .committed.)))
