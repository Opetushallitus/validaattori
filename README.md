# validaattori

Datavalidaatio sekä selaimelle että serverille samalla koodilla.

## testien ajaminen

    lein midje

## hakurekisteri-testisivu

Buildaa hakurekisterin validaattori

    lein with-profile hakurekisteri cljsbuild auto

Avaa testisivu

    open hakurekisteri/testpage/index.html

Avaa tiedostovalitsimella testitiedosto `esimerkki-arvosanat.xml`.
