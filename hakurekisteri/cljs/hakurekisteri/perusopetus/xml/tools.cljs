(ns hakurekisteri.perusopetus.xml.tools)

(def text-content ::text)

(extend-type js/NodeList
  ILookup
  (-lookup ([nl k] (-lookup nl k nil))
           ([nl k not-found] (if (number? k)
                                (if-let [result (.item nl k)] result not-found)
                                not-found)))
  ICounted
  (-count [nl] (.-length nl))

  ISeqable
  (-seq [nl]
        (letfn [(nl-seq ([nl] (nl-seq nl 0))
                        ([nl n]
                         (when-not (= n (count nl))
                           (cons (get nl n) (lazy-seq (nl-seq nl (inc n)))))))]
          (nl-seq nl)))

  ISeq
  (-first [nl]
    (first (seq nl)))
  (-rest [nl]
    (rest (seq nl))))

(defn parse-selector [selector]
  (cond
   (keyword? selector) (name selector)
   (sequential? selector) (apply str (interpose "," (map parse-selector selector)))
   :default selector))


(extend-type js/Element
  ILookup
  (-lookup ([e s] (-lookup e s nil))
           ([e s not-found]
            (if
             (= ::text s)
             (.-textContent e)
             (if-let [result (.querySelector e (parse-selector s))]
               result
               not-found))))
  INamed
  (-name [e] (.-tagName e))
  (-namespace [_] nil))

(defn xml-select
  ([xml selector]
   (.querySelectorAll xml (parse-selector selector)))
  ([xml selector & selectors]
   (xml-select xml (cons selector selectors))))

(defn text-select [el & selectors]
  (apply str (mapcat text-content (apply xml-select (cons el selectors)))))

(defn parse-str [xml]
  (.parseFromString (js/DOMParser.)  xml "application/xml"))
