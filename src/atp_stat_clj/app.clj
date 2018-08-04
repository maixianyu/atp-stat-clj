(ns atp-stat-clj.app
  (:require [atp-stat-clj.core :as atp-core])
  (:require [clojure.string :as str]))

; data set
(def stat-2018
  (atp-core/grep-item
    atp-core/all-stats
    ["tourney_year_id"]
    #(str/includes? (nth % 0) "2018")))
(count stat-2018)
(count atp-core/all-stats)

(def map-winner-count
  (atp-core/gen-map stat-2018 "winner_name" #(if (empty? %) 0 1)))

(def map-loser-count
  (atp-core/gen-map stat-2018 "loser_name" #(if (empty? %) 0 1)))

(sort #(if (< (val %1) (val %2))
         false
         true)
      map-winner-count)

(def map-winner-loser-count
  (atp-core/merge-map map-winner-count map-loser-count #(+ %1 %2)))

(def map-winner-rate
  (atp-core/merge-map map-winner-count map-loser-count #(if (= 0 %2)
                                                 1
                                                 (/ %1 (+ %1 %2)))))


(def grandslam
  (set ["australian-open" "roland-garros" "wimbledon" "us-open"]))
grandslam


(def roger-win-dj-in-grandslam
  (atp-core/grep-item
    atp-core/all-stats
    ["winner_name" "loser_name" "tourney_slug"]
    #(and (str/includes? (nth % 0) "Roger Federer")
          (str/includes? (nth % 1) "Novak Djokovic")
          (contains? grandslam (nth % 2)))))

(def roger-vs-dj-in-grandslam
  (atp-core/grep-item
    atp-core/all-stats
    ["winner_name" "loser_name" "tourney_slug"]
    #(and (or (and (str/includes? (nth % 1) "Roger Federer")
                   (str/includes? (nth % 0) "Novak Djokovic"))
              (and (str/includes? (nth % 1) "Novak Djokovic")
                   (str/includes? (nth % 0) "Roger Federer")))
          (contains? grandslam (nth % 2)))))

(count roger-win-dj-in-grandslam)
(count roger-vs-dj-in-grandslam)
(last roger-win-dj-in-grandslam)
roger-vs-dj-in-grandslam
