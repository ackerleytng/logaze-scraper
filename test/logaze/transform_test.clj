(ns logaze.transform-test
  (:require [logaze.transform :as t]
            [clojure.test :refer [deftest is]]))

(deftest processor-cache-test
  (is (= "4M"
         (t/processor-cache "Intel® Core™ i3-8130U Processor (4M Cache, up to 3.40 GHz)")))
  (is (= "2M"
         (t/processor-cache "AMD Ryzen 5 2500U Processor (4C, 2.0 / 3.6GHz, 2MB)"))))

(deftest hard-drive-size-test
  (is (= "1TB"
         (t/hard-drive-size "1TB 5400RPM 2.5\" Hard Drive")))
  (is (= "512GB"
         (t/hard-drive-size "512GB 5400RPM 2.5\" Hard Drive"))))

(deftest processor-range-test
  (is (= "i3"
         (t/processor-range "Intel® Core™ i3-8130U Processor (4M Cache, up to 3.40 GHz)")))
  (is (= "Ryzen 5"
         (t/processor-range "AMD Ryzen 5 2500U Processor (4C, 2.0 / 3.6GHz, 2MB)")))
  (is (= "Celeron"
         (t/processor-range "Intel® Celeron® Processor 3865U (2M Cache, 1.80 GHz)"))))

(deftest regression-test
  (is
   (=
    {:keyboard "US English"
     :processor-cache "4M"
     :hard-drive-size "1TB"
     :wireless "11AC 1x1 Wi-Fi + Bluetooth combo"
     :refurbished false
     :memory "4GB DDR4 2133Mhz / soldered to systemboard and 2GB DDR4 2133 SoDIMM Memory"
     :screen-size 15.6
     :processor-range "i3"
     :screen-supports-touch false
     :battery "30Wh Li-Ion Battery"
     :hard-drive "1TB 5400RPM 2.5\" Hard Drive"
     :bluetooth "Bluetooth"
     :warranty "1 Year Standard Depot Warranty"
     :resolution "1366x768"
     :screen-has-ips false
     :operating-system "Windows 10 Home 64 - English"
     :display-type "15.6\" HD (1366x768)MultiTouch, anti-glare, LED backlight w/720p Camera"
     :orig-price 469.99
     :graphics "Intel UHD Graphics 620"
     :pen "None"
     :memory-size "4GB"
     :processor-brand "Intel"
     :pointing-device "One-piece multi-touch touchpad"
     :camera "720p HD"
     :memory-soldered true
     :part-number "81DJ0007US"
     :hard-drive-type "Hard Drive"
     :processor "Intel® Core™ i3-8130U Processor (4M Cache, up to 3.40 GHz)"
     :price 305.49
     :ac-adapter "45 watt AC"
     :model "IdeaPad 330-15IKBR Touch -  New"}
    (t/transform-attributes
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
      :model "IdeaPad 330-15IKBR Touch -  New"}))))
