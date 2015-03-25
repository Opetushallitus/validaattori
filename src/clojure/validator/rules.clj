(ns validator.rules
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

(defn failed-rule-id [{:keys [id]}]
  (if-let [idns (and (instance? clojure.lang.Named id) (namespace id))]
    (str idns "." (name id))
    (name id)))



(defrecord ValidationFailure [resource rule]
  ^:clj
  validator.api.ValidationResult
  ^:clj
  (getResource [_] resource)
  ^:clj
  (getFailedRule [_] (failed-rule-id rule)))

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
  (message [this resource] (->ValidationFailure resource this)))

(defn ruleset [& rules]
  (RulesetData. rules))

(defn rule [name & {:keys [applies validator] :or {applies (constantly true)}}]
  (map->RuleData {:id name :applies applies :validator validator}))
