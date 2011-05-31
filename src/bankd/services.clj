(ns bankd.services
  (:use [bankd.command-bus :only [execute-command]])
  (:require [bankd.commands :as cmd]))

(defn create-client [params]
  (execute-command cmd/create-client params))

(defn name-client [params]
  (execute-command cmd/name-client (:id params) (:name params)))
