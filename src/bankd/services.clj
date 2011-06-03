(ns bankd.services
  (:use [bankd.command-bus :only [execute-command]])
  (:require [bankd.commands :as cmd]
            [bankd.reports :as reports]))

(defn create-client [params]
  (execute-command cmd/create-client params))

(defn name-client [params]
  (execute-command cmd/name-client (:id params) (:version params) (:name params)))

(defn show-client [params]
  (reports/show-client (:id params)))
