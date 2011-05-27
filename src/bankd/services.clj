(ns bankd.services
  (:use [bankd.core :only [execute-command]])
  (:require [bankd.commands :as cmd]))

(defn create-client [params]
  (execute-command cmd/create-client))

(defn name-client [params]
  (execute-command cmd/name-client (:id params) (:name params)))
