(ns particle-life.core.desktop-launcher
  (:require [particle-life.core :refer :all])
  (:import [com.badlogic.gdx.backends.lwjgl LwjglApplication]
           [org.lwjgl.input Keyboard])
  (:gen-class))

(def window_w 1600)
(def window_h 900)
(def steps_per_frame_normal 10)

(defn -main
  []
  (LwjglApplication. particle-life-game "particle-life" window_w window_h)
  (Keyboard/enableRepeatEvents true))
