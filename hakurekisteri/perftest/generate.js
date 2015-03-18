#!/usr/bin/env node
Mustache = require("mustache")
fs = require("fs")
template = fs.readFileSync("esimerkki-arvosanat.mustache", { encoding: "UTF-8" })
count = 10000

henkilot = []
for (var i = 0; i < count; i++) {
  var henkilo = {hetu:"111111-111L"}
  henkilot.push(henkilo)
}

data = {
 henkilot: henkilot
}

fs.writeFileSync("perfdata.xml", Mustache.render(template, data), { encoding: "UTF-8" })
