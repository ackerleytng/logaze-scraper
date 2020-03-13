(ns logaze.extract-test
  (:require [logaze.extract :as e]
            [logaze.test-helpers :as t]
            [clojure.test :refer [deftest is]]))

(deftest regression-test
  (is
   (=
    {:stock-status "Only 1 left in stock.",
     :wireless
     "Intel Wireless-AC 9260, Wi-Fi 2x2 802.11ac + Bluetooth 5.0, M.2 card",
     :memory "8GB DDR4 2666Mhz SoDIMM Memory",
     :battery "3 Cell Li-Ion Battery (42Wh)",
     :hard-drive "256GB Solid State Drive M.2",
     :bluetooth "Bluetooth",
     :warranty "1 Year Standard Depot Warranty",
     :operating-system "Windows 10 Professional 64 - English",
     :display-type "14.0\" FHD (1920x1080) IPS Anti-glare w/HD720p Camera",
     :orig-price "$741.99",
     :graphics "Intel® UHD Graphics 620",
     :pointing-device
     "TrackPoint pointing device and buttonless Mylar surface multi-touch touchpad w/Touch style fingerprint reader on the palm rest",
     :part-number "Part Number:  20NGCTR1WW-PF1S6DNH",
     :processor
     "Intel® Core™ i5-8265U Processor (6M Cache, up to 3.90 GHz)",
     :price "$630.69",
     :model "ThinkPad E490s -  Refurbished"}
    (e/extract (t/resource-filename "test/fixtures/2020-03-13-available.html"))))
  (is
   (=
    {:stock-status "Out of Stock",
     :wireless
     "Intel Wireless-AC 9560, Wi-Fi 2x2 802.11ac + Bluetooth 5.0, M.2 card",
     :memory "16GB 2133MHz LPDDR3 / soldered to systemboard",
     :battery "4 Cell Li-Ion Internal Battery (51Wh)",
     :hard-drive "256GB Solid State Drive M.2",
     :bluetooth "Bluetooth",
     :warranty "1 Year Standard Depot Warranty",
     :operating-system "Windows 10 Home 64 - English",
     :display-type
     "14.0\" WQHD (2560x1440) IPS Anti-glare w/HD720p Cameras",
     :orig-price "$1,215.99",
     :graphics "Intel® UHD Graphics 620",
     :pointing-device
     "TrackPoint pointing device and buttonless glass surface multi-touch touchpad w/Touch style match on chip fingerprint reader on the palm rest",
     :part-number "Part Number:  20QDCTO1WW-PF1YRGC7",
     :processor
     "Intel® Core™ i7-8565U Processor (8M Cache, up to 4.60 GHz)",
     :price "$608.00",
     :model "ThinkPad X1 Carbon (7th Gen) -  New"}
    (e/extract (t/resource-filename "test/fixtures/2020-03-13-out-of-stock.html"))))
  (is
   (= {:orig-price nil, :price nil, :model nil, :part-number nil, :stock-status nil}
      (e/extract (t/resource-filename "test/fixtures/2019-10-20-no-longer-available.html")))))
