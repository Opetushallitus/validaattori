(ns validator.rules-test
  (:use midje.sweet
        validator.rules)
  (:require [validator.core :refer [Validatable suppressed]]))


(extend-type clojure.lang.Keyword
  Validatable
  (validate [this] :unifinished)
  (suppressed [this] :unifinished))


(extend-type midje.data.metaconstant.Metaconstant
  Validatable
  (validate [this] :unifinished)
  (suppressed [this] :unifinished))


(extend-type midje.data.metaconstant.Metaconstant
  Rule
  (applies? [rule resource] :unifinished)
  (problems? [rule resource] :unifinished)
  (id [rule] "id of the rule" :unifinished))

(facts "about ruleset"
       (fact "it returns a single applicable rule for resource"
             (find-rules
              (ruleset
               ...rule...) :resource) => (contains [...rule...])
             (provided
              (applies? ...rule... :resource) => true))

       (fact "it returns multiple applicable rules"
             (find-rules (ruleset
                          ...rule1...
                          ...rule2...) :resource) => (contains [...rule1... ...rule2...])
             (provided
              (applies? ...rule1... :resource) => true
              (applies? ...rule2... :resource) => true))


       (fact "it returns only applicable rules"
             (find-rules (ruleset
                          ...rule1...
                          ...rule2...) :resource) => (just [...rule1...])
             (provided
              (applies? ...rule1... :resource) => true
              (applies? ...rule2... :resource) => false)))


(facts "about rule"
       (fact "it returns the a ValidationFailure record with problem resource and the failed rule"
             (problems?
              (rule "example" :validator (constantly false))
              ...resource...) => (contains [(just {:resource ...resource... :rule (contains {:id "example"})})] ))
       (fact "it returns an empty vector for resource with no problems"
             (problems?
              (rule "example" :validator (constantly true))
              ...resource...) => [])
       (fact "it applies allways by default"
             (applies?
               (rule "example" :validator (constantly true))
               ...resource...) => true)
       (fact "it retuns true for applicable resource when checking applicability"
             (applies?
              (rule "example" :applies (partial = ...resource...) :validator (constantly false))
              ...resource...) => true)
       (fact "it returns false for unapplicable resource when checking applicability"
             (applies?
              (rule "example" :applies (partial = ...resource...) :validator (constantly false))
              ...resource2...) => false)
       (fact "it returns false for resource which supresses it when checking applicability"
             (applies?
              (rule "example" :applies (partial = ...resource...) :validator (constantly false))
              ...resource...) => false
             (provided
              (suppressed ...resource...) => #{"example"})))
