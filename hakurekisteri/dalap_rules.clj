{
  ["src/clojure/validator/core.clj" "gen-cljs/validator/core.cljs"] []
  ["src/clojure/validator/rules.clj" "gen-cljs/validator/rules.cljs"] [(dalap/when #(= %1 '(instance? clojure.lang.Named id))) (dalap/transform (constantly '(implements? INamed id)))]
  ["hakurekisteri/src/hakurekisteri/perusopetus.clj" "gen-cljs/hakurekisteri/perusopetus.cljs"] ['clojure.lang.IFn 'IFn]}
