(ns particle-life.core
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [play-clj.ui :refer :all]))

;;;;;;;;;;; Simulation Params ;;;;;;;;;;
(def g-const 1.0)
(def mass-variance 1.0)
(def cut-off-zone 200.0)
(def p-amount 400)

;;;;;;;; N Body Simulation ;;;;;;;;;;
; Init the particle list
(defn init-particles [amount texture]
  (for [x (range amount)]
    (let [mass (Math/max 5 (rand-int (* 5 mass-variance)))]
      (assoc texture
        :x (rand-int 1600)
        :y (rand-int 900)
        :m mass
        :vx 0 :vy 0
        :id :particle))))

; Compute the gravitational effect of p2 on p1 and update p1
(defn grav-eq [p1 p2]
  (if (and (= (:id p1) :particle) (= (:id p2) :particle))
    (let [dx (- (:x p1) (:x p2))
          dy (- (:y p1) (:y p2))
          dist (max cut-off-zone (Math/sqrt (+ (* dx dx) (* dy dy))))
          f (/ (* (:m p1) (:m p2) g-const) (* dist dist))
          dt (graphics! :get-delta-time)]
      (assoc p1
        :x (- (:x p1) (* (:vx p1) dt))
        :y (- (:y p1) (* (:vy p1) dt))
        :vx (+ (:vx p1) (* f (/ dx dist)))
        :vy (+ (:vy p1) (* f (/ dy dist)))))
    p1))

; Apply gravity to p with all of the particles in ps
(defn process-entity [p ps i]
  (case (:id p)
    :particle
    (if (= i (count ps))
      p
      (recur (grav-eq p (nth ps i)) ps (+ i 1)))
    :fps (doto p (label! :set-text (str (game :fps) " FPS")))
    :num-of-particles (doto p (label! :set-text (str (- (count ps) 2) " particles")))
    p))

; Update every particle
(defn update-entities [ps]
  (pmap #(process-entity % ps 0) ps))

;;;;;;;;;;;; Drawing ;;;;;;;;;;;;
(defn init-entities []
  (conj (init-particles p-amount (texture "particle.png"))
        (assoc (label "" (color :white)) :id :fps
                                             :x 10
                                             :y (- (game :height) 40))
        (assoc (label "" (color :white)) :id :num-of-particles
                                             :x 10
                                             :y (- (game :height) 20))))

(defscreen main-screen
           :on-show
           (fn [screen entities]
             (update! screen :renderer (stage))
             (init-entities))

           :on-render
           (fn [screen entities]
             (clear!)
             (render! screen (update-entities entities)))

           :on-key-down
           (fn [screen entities]
             (cond
               (= (:key screen) (key-code :r))
               (init-entities))))

(defgame particle-life-game
         :on-create
         (fn [this]
           (set-screen! this main-screen)))

