# clina

yet another lightweight git host service come from lisp world

## Running from Source Code

* setup repository path in your ~/.*shrc file use env variable

```
export CLINA_DATA="basepath"
```

* start jetty server

```
lein ring server
lein ring server-headless
```

## License

Copyright © 2015 jihui

Distributed under the Eclipse Public License, the same as Clojure.
