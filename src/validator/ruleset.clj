(ns validator.ruleset
  (:require [validator.core :refer [suppressed]]))

(defprotocol Ruleset
  "ruleset for validation"
  (find-rules [rules resource] "find applicable rules for a resource"))

(defprotocol Rule
  "a single rule for validation"
  (applies? [rule resource] "if this rule applies to given resource")
  (problems? [rule resource] "find problems if resource")
  (id [rule] "id of the rule")
  (message [rule resource] "compose a message for problematic resource"))

(defrecord RulesetData [rules]
  Ruleset
  (find-rules [this resource]
              (filter
               #(applies? %1 resource)
               (:rules this))))

(defrecord RuleData [id applies validator]
  Rule
  (applies? [this resource] (and
                             ((:applies this) resource)
                             (not ((suppressed resource) (:id this)))))

  (problems? [this resource] (if
                               ((:validator this) resource)
                               []
                               [(message this resource)]))
  (id [this] (:id this))
  (message [this resource] [resource this]))

(defn ruleset [& rules]
  (RulesetData. rules))

(defn rule [name & {:keys [applies validator] :or {applies (constantly true)}}]
  (map->RuleData {:id name :applies applies :validator validator}))
