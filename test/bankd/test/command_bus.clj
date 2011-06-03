(ns bankd.test.command-bus
  (:use bankd.command-bus
        midje.sweet))

(unfinished call-me)

;.;. FAIL at (NO_SOURCE_FILE:1)
;.;. You claimed the following was needed, but it was never used:
;.;.     (call-me .result.)
(facts "execute-command"
  (fact "executes the command and returns the result"
    (execute-command .cmd. .arg.) => .result.
    (provided
      (.cmd. .arg.) => .result.))
  (fact "saves and publishes any created events"
    (execute-command .cmd. .arg.) => .result.
    (provided
      (.cmd. .arg.) => .result.
      (commit-repository! .result.) => .committed.
      (call-me .result.) => .what-evs.)))
