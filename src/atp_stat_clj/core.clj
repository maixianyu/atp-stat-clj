(ns atp-stat-clj.core
  (:require [clojure-csv.core :as csv])
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as str]))

; config file path
(def dir "resources/csv/2_match_scores/")
(def file-list "file.list")
(def item-map-file "match_scores_column_titles.txt")

(def item-map
  (zipmap
    (line-seq (io/reader (str dir item-map-file)))
    (range (count (line-seq (io/reader (str dir item-map-file)))))
    ))

(defn file-path-list [dir-path file-list]
  (map #(str dir-path %)
    (line-seq
      (io/reader (str dir-path file-list)))))

; func to read a csv file
(defn read-csvfile [s]
  (csv/parse-csv (io/reader s)))

; func to read many csv file
(defn read-and-merge-csv-file [dir-path file-list]
  (reduce
    concat
    (map read-csvfile (file-path-list dir-path file-list))))

; get all-stats
(def all-stats (read-and-merge-csv-file dir file-list))

(count all-stats)

; item-name eg. "order-id"
(defn grep-item [stat-seq item-name item-map pred]
  (filter
    #(pred (get % (get item-map item-name)))
    stat-seq))

; key-item eg. "play-name"
(defn gen-map [stat-seq key-idx pred]
  (loop [inner-seq stat-seq
         res-map {}]
    (if (empty? inner-seq)
      res-map
      (recur (next inner-seq)
             (assoc res-map
               (get (first inner-seq) key-idx)
               (+ (get res-map (get (first inner-seq) key-idx) 0) (pred (first inner-seq))))))))

(def map-winner-count
  (gen-map all-stats (get item-map "winner_name") #(if (empty? %) 0 1)))

(def map-loser-count
  (gen-map all-stats (get item-map "loser_name") #(if (empty? %) 0 1)))

(sort #(if (< (val %1) (val %2))
         false
         true)
      map-winner-count)

(defn merge-map [map-1 map-2 pred]
  (loop [seq-keys (keys (into map-1 map-2))
         res-map {}]
    (if (empty? seq-keys)
      res-map
      (recur (next seq-keys)
             (assoc res-map
               (first seq-keys)
               (pred (get map-1 (first seq-keys) 0)
                     (get map-2 (first seq-keys) 0)))))))
(def map-winner-loser-count
  (merge-map map-winner-count map-loser-count #(+ %1 %2)))

(def map-winner-rate
  (merge-map map-winner-count map-loser-count #(if (= 0 %2)
                                                 1
                                                 (/ %1 (+ %1 %2)))))

(sort #(if (< (val %1) (val %2))
         false
         true)
      map-winner-loser-count)

(sort #(if (< (val %1) (val %2))
         false
         true)
      map-winner-rate)


(val (first (assoc {} "nobody" 1)))
(into {:a 1 :b 2 :c 3} {:a 2})


(get item-map "winner_name")


(def win_count_roger
  (count (grep-item all-stats
             "winner_name"
             item-map
             #(str/includes? % "Roger Federer"))))


