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
(defn grep-item [stat-seq item-name-seq pred]
  (defn make-data-vec [d-vec i-vec]
    (map #(nth d-vec %) i-vec))
  (loop [seq-tmp item-name-seq
         idx-vec []]
    (if (empty? seq-tmp)
      (filter
        #(pred (make-data-vec % idx-vec))
        stat-seq)
      (recur (next seq-tmp)
             (conj idx-vec (get item-map (first seq-tmp)))))))


; key-item eg. "play-name"
(defn gen-map [stat-seq key-name pred]
  (let [key-idx (get item-map key-name)]
    (loop [inner-seq stat-seq
           res-map {}]
      (if (empty? inner-seq)
        res-map
        (recur (next inner-seq)
               (assoc res-map
                 (get (first inner-seq) key-idx)
                 (+ (get res-map (get (first inner-seq) key-idx) 0) (pred (first inner-seq)))))))))



; merge two maps
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






