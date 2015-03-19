# validaattori

Datavalidaatio sekä selaimelle että serverille samalla koodilla.

## testien ajaminen

    lein midje
    hakurekisterin testit mukaan:
    lein hr midje

## hakurekisteri-testisivu

Buildaa hakurekisterin validaattori (prod build)

    lein hr-prod
    open hakurekisteri/testpage/prod.html

Tai buildaa dev-build

    lein hr-dev
    open hakurekisteri/testpage/index.html

Avaa tiedostovalitsimella testitiedosto `esimerkki-arvosanat.xml`.
