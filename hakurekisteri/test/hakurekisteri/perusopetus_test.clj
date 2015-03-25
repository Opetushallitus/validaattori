(ns hakurekisteri.perusopetus-test
  (:use midje.sweet
        hakurekisteri.perusopetus
        validator.rules)
  (:require [validator.core :refer [Validatable validate]]))



(defn fails-rule [resource rule]
  (contains [(just {:resource resource :rule (contains {:id rule})})]))


(defmulti create-arvosana type)
(defmethod create-arvosana clojure.lang.PersistentHashSet [s]
  (rand-nth (seq s)))
(defmethod create-arvosana :default [s]
  s)

(defn todistus [s] (->Todistus
                    (->Suoritus "1.2.246.562.13.62959769647" "VALMIS")
                    (map #(->Arvosana (create-arvosana %1) 8 "" false) s)
                    #{}))

(def empty-todistus (todistus []))

(facts "about mandatory subjects in perusopetuksen todistus"
       (fact "Äidinkieli is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :hakurekisteri.perusopetus/mandatory-ai))
       (fact "A1 kieli is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :hakurekisteri.perusopetus/mandatory-a1))
       (fact "Matematiikka is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :hakurekisteri.perusopetus/mandatory-ma))
       (fact "Käsityö is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :hakurekisteri.perusopetus/mandatory-ks))
       (fact "Terveystieto is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :hakurekisteri.perusopetus/mandatory-te))
       (fact "Kotitalous is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :hakurekisteri.perusopetus/mandatory-ko))
       (fact "Biologia is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :hakurekisteri.perusopetus/mandatory-bi))
       (fact "Musiikki is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :hakurekisteri.perusopetus/mandatory-mu))
       (fact "Liikunta is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :hakurekisteri.perusopetus/mandatory-li))
       (fact "Katsomustieto is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :hakurekisteri.perusopetus/mandatory-kt))
       (fact "Maantieto is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :hakurekisteri.perusopetus/mandatory-ge))
       (fact "Kuvaamataito is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :hakurekisteri.perusopetus/mandatory-ku))
       (fact "Kemia is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :hakurekisteri.perusopetus/mandatory-ke))
       (fact "Fysiikka is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :hakurekisteri.perusopetus/mandatory-fy))
       (fact "B1 or A2 kieli is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :hakurekisteri.perusopetus/mandatory-a2-or-b1))
       (fact "Käsityö is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :hakurekisteri.perusopetus/mandatory-ks))
       (fact "Yhteiskuntaoppi is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :hakurekisteri.perusopetus/mandatory-yh))
       (fact "Historia is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :hakurekisteri.perusopetus/mandatory-hi)))

(fact "valid todistus has no problems"
      (validate (todistus mandatory-subjects)) => [])
