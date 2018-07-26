(ns atp-stat-clj.core
  (:require [clojure-csv.core :as csv])
  (:require [clojure.java.io :as io]))


(with-open [r (io/reader "resources/csv/3_match_stats/match_stats_2018_0.csv")]
  (def atp-csv (csv/parse-csv r)))

(println atp-csv)

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))
