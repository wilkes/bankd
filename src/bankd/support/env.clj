(ns bankd.support.env
  (:use [bankd.support.event-bus :only [in-process-bus]]
        [bankd.support.event-storage :only [*event-log*]]
        [bankd.support.reporting :only [*reports*]]))

(defn reset-env! []
  (swap! *event-log* (constantly {}))
  (in-process-bus)
  (swap! *reports* (constantly {})))
