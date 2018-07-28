(ns atp-stat-clj.core
  (:require [clojure-csv.core :as csv])
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as str]))


(def freader (io/reader "resources/csv/3_match_stats/match_stats_2018_0.csv"))

(def atp-csv (csv/parse-csv freader))

atp-csv

(str/includes? "0" "0")

(map (fn [x] x) (seq [1 2 3]))

(def res-csv
  (filter (fn [x]
            (str/includes? (get x 0) "0"))
          atp-csv))

(println res-csv)

