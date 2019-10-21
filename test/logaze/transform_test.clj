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
         (t/hard-drive-size "512GB 5400RPM 2.5\" Hard Drive")))
  (is (= "1.016TB"
         (t/hard-drive-size "1.016TB (1 x 1TB 5400rpm and 1 x 16GB Optane M.2) Drives")))
  (is (= "500GB"
         (t/hard-drive-size "500G_7MM_5400RPM"))))

(deftest hard-drive-type-test
  (is (= "Multi"
         (t/hard-drive-type "1.016TB (1 x 1TB 5400rpm and 1 x 16GB Optane M.2) Drives")))
  (is (= "HDD"
         (t/hard-drive-type "1TB 5400RPM 2.5\" Hard Drive")))
  (is (= "HDD"
         (t/hard-drive-type "512GB 5400RPM 2.5\" Hard Drive")))
  (is (= "SSD"
         (t/hard-drive-type "512GB SATA 2.5\" Solid State Drive")))
  (is (= "eMMC"
         (t/hard-drive-type "128GB eMMC (embedded Multi Media Card) flash memory")))
  (is (= "HDD"
         (t/hard-drive-type "500GB 7200RPM Serial ATA"))))

(deftest processor-range-test
  (is (= "i3"
         (t/processor-range "Intel® Core™ i3-8130U Processor (4M Cache, up to 3.40 GHz)")))
  (is (= "Ryzen 5"
         (t/processor-range "AMD Ryzen 5 2500U Processor (4C, 2.0 / 3.6GHz, 2MB)")))
  (is (= "Celeron"
         (t/processor-range "Intel® Celeron® Processor 3865U (2M Cache, 1.80 GHz)")))
  (is (= "Pentium"
         (t/processor-range "Intel® Pentium® Silver N5000 Processor (4M Cache, up to 2.70 GHz)")))
  (is (= "A6"
         (t/processor-range "AMD A6-9500B APU Processor")))
  (is (= "Atom"
         (t/processor-range "Intel® Atom™ x7-Z8750 Processor(2M Cache, up to 2.56 GHz)")))
  (is (= "Ryzen R3"
         (t/processor-range "AMD Ryzen R3-2200U Processor (2C, 2.5 / 3.4 Ghz, 1MB)")))
  (is (= "PRO A10"
         (t/processor-range "AMD PRO A10-9700B Processor"))))

(deftest fix-resolution-test
  (is (= "1920x1200" (t/fix-resolution "1920 x 1200")))
  (is (= "10.1 WUXGA (1920x1200) IPS Multi-touch w/ Front 1.2mp and Rear 5.0mp Camera"
         (t/fix-resolution
          "10.1 WUXGA (1920 x 1200) IPS Multi-touch w/ Front 1.2mp and Rear 5.0mp Camera")))
  (let [desc "15.6\" HD (1366x768) anti-glare, LED backlight w/720p Camera"]
    (is (= desc (t/fix-resolution desc)))))

(deftest product-type-test
  (is (= "Scratch and Dent" (t/product-type "ThinkPad X1 Carbon (6th Gen) - Scratch and Dent")))
  (is (= "Refurbished" (t/product-type "ThinkPad P52s - Refurbished")))
  (is (= "New" (t/product-type "ThinkPad X1 Tablet (3rd Gen) - New"))))

(deftest clean-model-test
  (is (= "ThinkPad X1 Carbon (6th Gen)"
         (t/clean-model "ThinkPad X1 Carbon (6th Gen) - Scratch and Dent")))
  (is (= "ThinkPad P52s"
         (t/clean-model "ThinkPad P52s - Refurbished")))
  (is (= "ThinkPad X1 Tablet (3rd Gen)"
         (t/clean-model "ThinkPad X1 Tablet (3rd Gen) - New"))))

(deftest regression-test
  (is
   (=
    {:keyboard "Keyboard - US English"
     :processor-cache "4M"
     :hard-drive-size "1TB"
     :wireless "11AC 1x1 Wi-Fi + Bluetooth combo"
     :product-type "New"
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
     :hard-drive-type "HDD"
     :processor "Intel® Core™ i3-8130U Processor (4M Cache, up to 3.40 GHz)"
     :price 305.49
     :ac-adapter "45 watt AC"
     :model "IdeaPad 330-15IKBR Touch"}
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
