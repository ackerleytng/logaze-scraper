(ns logaze.extract-test
  (:require [logaze.extract :as e]
            [logaze.test-helpers :as t]
            [clojure.test :refer [deftest is]]))

(deftest regression-test
  (is
   (=
    {:keyboard "Keyboard - US English"
     :wireless "11AC 1x1 Wi-Fi + Bluetooth combo"
     :memory "4GB DDR4 2133Mhz / soldered to systemboard and 2GB DDR4 2133 SoDIMM Memory"
     :battery "30Wh Li-Ion Battery"
     :hard-drive "1TB 5400RPM 2.5\" Hard Drive"
     :bluetooth "Bluetooth"
     :warranty "1 Year Standard Depot Warranty"
     :operating-system "Windows 10 Home 64 - English"
     :display-type "15.6\" HD (1366x768)MultiTouch, anti-glare, LED backlight w/720p Camera"
     :orig-price "$469.99"
     :graphics "Intel UHD Graphics 620"
     :pen "None"
     :pointing-device "One-piece multi-touch touchpad"
     :camera "720p HD"
     :part-number "Part Number:  81DJ0007US"
     :processor "Intel® Core™ i3-8130U Processor (4M Cache, up to 3.40 GHz)"
     :price "$305.49"
     :ac-adapter "45 watt AC"
     :model "IdeaPad 330-15IKBR Touch -  New"}
    (e/extract (t/resource-filename "test/fixtures/2019-10-20-available.html"))))
  (is
   (= {:orig-price nil, :price nil, :model nil, :part-number nil}
      (e/extract (t/resource-filename "test/fixtures/2019-10-20-no-longer-available.html")))))
