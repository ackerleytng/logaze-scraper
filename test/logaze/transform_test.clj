(ns logaze.transform-test
  (:require [logaze.transform :as t]
            [clojure.test :refer [deftest is]]))

(deftest model-test
  (is (= "Ideapad 3"
         (t/model "Notebook Ideapad 3")))
  (is (= "ThinkPad P16 Gen 1"
         (t/model "Workstation P16 Gen 1")))
  (is (= "ThinkPad T15p Gen 3"
         (t/model "Notebook Workstation T15p Gen 3")))
  (is (= "ThinkPad"
         (t/model "Lenovo ThinkPad"))))

(deftest memory-size-test
  (is (= "8GB"
         (t/memory-size "8 GB DDR4 2667MHz (Soldered)")))
  (is (= nil
         (t/memory-size "no match!"))))

(deftest storage-size-test
  (is (= "1TB"
         (t/storage-size "1TB 5400RPM 2.5\" Hard Drive")))
  (is (= "512GB"
         (t/storage-size "512GB 5400RPM 2.5\" Hard Drive")))
  (is (= "1.016TB"
         (t/storage-size "1.016TB (1 x 1TB 5400rpm and 1 x 16GB Optane M.2) Drives")))
  (is (= "500GB"
         (t/storage-size "500G_7MM_5400RPM")))
  (is (= "256GB"
         (t/storage-size "256 GB PCIe SSD")))
  (is (= nil
         (t/storage-size "no match!"))))

(deftest storage-type-test
  (is (= "Multi"
         (t/storage-type "1.016TB (1 x 1TB 5400rpm and 1 x 16GB Optane M.2) Drives")))
  (is (= "HDD"
         (t/storage-type "1TB 5400RPM 2.5\" Hard Drive")))
  (is (= "HDD"
         (t/storage-type "512GB 5400RPM 2.5\" Hard Drive")))
  (is (= "SSD"
         (t/storage-type "512GB SATA 2.5\" Solid State Drive")))
  (is (= "SSD"
         (t/storage-type "256GB Solid State M.2 Drive")))
  (is (= "eMMC"
         (t/storage-type "128GB eMMC (embedded Multi Media Card) flash memory")))
  (is (= "HDD"
         (t/storage-type "500GB 7200RPM Serial ATA")))
  (is (= nil
         (t/storage-type "no match!"))))

(deftest processor-brand-test
  (is (= "Intel"
         (t/processor-brand "Intel® Core™ i3-8130U Processor (4M Cache, up to 3.40 GHz)")))
  (is (= "AMD"
         (t/processor-brand "AMD Ryzen 5 2500U Processor (4C, 2.0 / 3.6GHz, 2MB)")))
  (is (= "MediaTek"
         (t/processor-brand "MediaTek MT8173C Processor (4C, 2x A72 @2.1GHz + 2x A53 @1.7GHz)")))
  (is (= nil
         (t/processor-cache "no match!"))))

(deftest processor-cache-test
  (is (= "4M"
         (t/processor-cache "Intel® Core™ i3-8130U Processor (4M Cache, up to 3.40 GHz)")))
  (is (= "2M"
         (t/processor-cache "AMD Ryzen 5 2500U Processor (4C, 2.0 / 3.6GHz, 2MB)")))
  (is (= "12M"
         (t/processor-cache "Intel® Xeon® E-2176M Processor (12M Cache, up to 4.40 GHz)")))
  (is (= "6M"
         (t/processor-cache "10th Generation Intel® Core™ i5-10210U Processor (1.60 GHz, up to 4.20 GHz with Turbo Boost, 4 Cores, 8 Threads, 6 MB Cache)")))
  (is (= nil
         (t/processor-cache "no match!"))))

(deftest processor-range-test
  (is (= "i3"
         (t/processor-range "Intel® Core™ i3-8130U Processor (4M Cache, up to 3.40 GHz)")))
  (is (= "Ryzen 5"
         (t/processor-range "AMD Ryzen 5 2500U Processor (4C, 2.0 / 3.6GHz, 2MB)")))
  (is (= "Ryzen 5"
         (t/processor-range "AMD Ryzen™ 5 PRO 3500U Mobile Processor")))
  (is (= "R7"
         (t/processor-range "R7 PRO 4700U Processor")))
  (is (= "R5"
         (t/processor-range "AMD R5 PRO 3500 Processor")))
  (is (= "Celeron"
         (t/processor-range "Intel® Celeron® Processor 3865U (2M Cache, 1.80 GHz)")))
  (is (= "Pentium"
         (t/processor-range "Intel® Pentium® Silver N5000 Processor (4M Cache, up to 2.70 GHz)")))
  (is (= "A6"
         (t/processor-range "AMD A6-9500B APU Processor")))
  (is (= "Atom"
         (t/processor-range "Intel® Atom™ x7-Z8750 Processor(2M Cache, up to 2.56 GHz)")))
  (is (= "Xeon"
         (t/processor-range "Intel® Xeon® E-2176M Processor (12M Cache, up to 4.40 GHz)")))
  (is (= "Ryzen R3"
         (t/processor-range "AMD Ryzen R3-2200U Processor (2C, 2.5 / 3.4 Ghz, 1MB)")))
  (is (= "PRO A10"
         (t/processor-range "AMD PRO A10-9700B Processor")))
  (is (= "Athlon"
         (t/processor-range "AMD Athlon Silver 3050e Processor (2C / 4T, 1.4 / 2.8GHz, 1MB L2 / 4MB L3)")))
  (is (= nil
         (t/processor-range "no match!"))))

(deftest resolution-test
  (is (= "1920x1200" (t/resolution "1920 x 1200")))
  (is (= "1920x1200"
         (t/resolution "10.1 WUXGA (1920 x 1200) IPS Multi-touch w/ Front 1.2mp and Rear 5.0mp Camera")))
  (is (= "1366x768"
         (t/resolution "15.6\" HD (1366x768) anti-glare, LED backlight w/720p Camera")))
  (is (= nil
         (t/resolution "no match!"))))

(deftest resolution-from-display-standards-test
  (is (= "1920x1200"
         (t/resolution-from-display-standards "13.3\" WUXGA Anti-Glare 300 nits")))
  (is (= "3840x2400"
         (t/resolution-from-display-standards "14\" WQUXGA OLED Anti-Reflective/Anti-Smudge 500 nits Multi-Touch")))
  (is (= "2240x1400"
         (t/resolution-from-display-standards "14\" 2.2K Anti-Glare 300 nits")))
  (is (= "1920x1080"
         (t/resolution-from-display-standards "15.6\" FHD TN Anti-Glare 250 nits"))))

(deftest extract-resolution-test
  ;; :display should take priority
  (is (= "1920x1080"
         (t/extract-resolution {:display "1920x1080" :screen-resolution "2240x1400"})))
  ;; :screen-resolution should be used if :display doesn't contain a resolution-like string
  (is (= "2240x1400"
         (t/extract-resolution {:display "FHD" :screen-resolution "2240x1400"})))
  ;; Finally, fall back to extracting based on display standards
  (is (= "3840x2400"
         (t/extract-resolution {:display "WQUXGA" :screen-resolution "something else"})))
  ;; nil if all else fails
  (is (= nil
         (t/extract-resolution {:display "no match" :screen-resolution "something else"}))))

(deftest product-type-test
  (is (= "Scratch and Dent" (t/product-type "ThinkPad X1 Carbon (6th Gen) - Scratch and Dent")))
  (is (= "Refurbished" (t/product-type "ThinkPad P52s - Refurbished")))
  (is (= "New" (t/product-type "ThinkPad X1 Tablet (3rd Gen) - New")))
  (is (= nil (t/product-type "no match!"))))

(deftest regression-test
  (is
   (=
    {:keyboard "Backlit - US English"
     :final-price "372.29"
     :processor-cache "6M"
     :wlan "Intel® 9560 802.11AC (2 x 2) & Bluetooth® 5.1"
     :product-number "20R3X001US"
     :memory "8 GB DDR4 2667MHz (Soldered)"
     :screen-size 13.3
     :processor-range "i5"
     :storage-size "256GB"
     :battery "Up to 14.1 hours"
     :web-price "729.99"
     :brand "ThinkPad"
     :warranty "1 Year Depot or Carry-in"
     :resolution "1920x1080"
     :screen-has-ips true
     :fingerprint-reader "Fingerprint Reader"
     :weight "Starting at 3.04 lbs (1.38 kg)"
     :operating-system "Windows 10 Pro 64"
     :orig-price 729.99
     :graphics "Integrated Intel® UHD Graphics"
     :product-code "20R3X001US"
     :url "https://www.lenovo.com/us/outletus/en/p/laptops/thinkpad/thinkpadl/L13-Clam-2019/20R3X001US"
     :memory-size "8GB"
     :processor-brand "Intel"
     :camera "720p HD"
     :storage "256 GB PCIe SSD"
     :display "13.3\" FHD (1920 x 1080) IPS, anti-glare, touchscreen, 300 nits"
     :inventory-status 1
     :memory-soldered true
     :available true
     :product-mkt-name "ThinkPad L13 Intel (13\") - Black"
     :touch-screen "Touch"
     :product-condition "Refurbished"
     :storage-type "SSD"
     :processor "10th Generation Intel® Core™ i5-10210U Processor (1.60 GHz, up to 4.20 GHz with Turbo Boost, 4 Cores, 8 Threads, 6 MB Cache)"
     :price 372.29
     :percentage-savings nil
     :model "ThinkPad L13 Intel (13\") - Black"}
    (t/transform-attributes
     {:keyboard "Backlit - US English"
      :final-price "372.29"
      :wlan "Intel® 9560 802.11AC (2 x 2) & Bluetooth® 5.1"
      :product-number "20R3X001US"
      :memory "8 GB DDR4 2667MHz (Soldered)"
      :battery "Up to 14.1 hours"
      :web-price "729.99"
      :brand "ThinkPad"
      :warranty "1 Year Depot or Carry-in"
      :fingerprint-reader "Fingerprint Reader"
      :weight "Starting at 3.04 lbs (1.38 kg)"
      :operating-system "Windows 10 Pro 64"
      :graphics "Integrated Intel® UHD Graphics"
      :product-code "20R3X001US"
      :url "/p/laptops/thinkpad/thinkpadl/L13-Clam-2019/20R3X001US"
      :camera "720p HD"
      :storage "256 GB PCIe SSD"
      :display "13.3\" FHD (1920 x 1080) IPS, anti-glare, touchscreen, 300 nits"
      :inventory-status 1
      :product-mkt-name "ThinkPad L13 Intel (13\") - Black"
      :touch-screen "Touch"
      :product-condition "Refurbished"
      :processor "10th Generation Intel® Core™ i5-10210U Processor (1.60 GHz, up to 4.20 GHz with Turbo Boost, 4 Cores, 8 Threads, 6 MB Cache)"}))))
