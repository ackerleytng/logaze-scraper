(ns logaze.openapi-test
  (:require [logaze.openapi :as o]
            [clojure.test :refer [deftest is]]))

(deftest keywordize-test
  (is (= :foo-bar-baz (o/keywordize "FooBarBaz")))
  (is (= :foo-bar-baz (o/keywordize "fooBarBaz")))
  (is (= :foo-bar-baz (o/keywordize "Foo Bar Baz")))
  (is (= :foo-bar-baz (o/keywordize "foo Bar baz")))
  (is (= :ac-adapter (o/keywordize "ACAdapter")))
  (is (= :ips-screen (o/keywordize "IPS Screen")))
  (is (= :ips-screen (o/keywordize "IPSScreen"))))

(deftest extract-flatten-detail-test
  (let [body "{\"xrid\":\"14f8b3d5fe9e21ac8c8020fdd8f83536\",\"success\":true,\"status\":200,\"msg\":null,\"params\":\"-\",\"data\":[{\"categoryCode\":\"laptops\",\"categoryName\":\"Laptops\",\"categoryListImage\":\"//p1-ofp.static.pub/fes/cms/2021/08/18/pzieiyt3as8bzktep20ahz1qitn7ak694928.png\",\"productList\":[{\"merchandisingFlag\":null,\"listImage\":[{\"imageAddress\":\"//p1-ofp.static.pub/fes/cms/2022/04/05/297rbv5tivys68qsdd570h0bau2liz634754.png\",\"imageName\":\"ideapad-1-14-intel-ice-blue-series-thumbnail.png\",\"imageCaption\":null}],\"thumbnail\":{\"imageAddress\":null,\"imageName\":\"22565277359_IdeaPad_1_14IGL05_Ice_Blue_IMG_20200327052500.png\",\"imageCaption\":null},\"productMktName\":\"IdeaPad 1 14IGL05 \",\"modelsNumber\":null,\"marketingStatus\":\"Available\",\"productNumber\":\"81VU00D5US\",\"inventoryMessage\":null,\"url\":\"/p/laptops/ideapad/ideapad-100/IdeaPad-1i-14IGL-5/81VU00D5US\",\"classification\":[{\"a\":\"Processor\",\"b\":\"Intel Celeron N4020 Processor (2C / 2T, 1.1 / 2.8GHz, 4MB)\"},{\"a\":\"Operating System\",\"b\":\"Windows 11 Home in S mode, English\"},{\"a\":\"Graphics\",\"b\":\"Integrated Intel UHD Graphics 600\"},{\"a\":\"Memory\",\"b\":\"4GB DDR4 2400MHz soldered to systemboard\"},{\"a\":\"Storage\",\"b\":\"128GB Solid State Drive M.2\"},{\"a\":\"Display\",\"b\":\"14\\\" FHD (1920 x 1080), TN, Anti-Glare, Non-Touch, 220 nits, LED Backlight, Narrow Bezel\"},{\"a\":\"Camera\",\"b\":\"0.3MP with Dual Array Microphone\"},{\"a\":\"Battery\",\"b\":\"Integrated Li-Polymer 32Wh battery\"},{\"a\":\"AC Adapter\",\"b\":\"45W\"},{\"a\":\"Pointing Device\",\"b\":\"ClickPad\"},{\"a\":\"Keyboard\",\"b\":\"Traditional, Grey with Number Pad - English (US)\"},{\"a\":\"WLAN\",\"b\":\"Intel® Wireless-AC 9560 2x2 AC\"},{\"a\":\"Bluetooth\",\"b\":\"Bluetooth® 5.0 or above\"},{\"a\":\"Warranty\",\"b\":\"1 Year Standard Depot Warranty\"},{\"a\":\"Color\",\"b\":\"Ice Blue\"},{\"a\":\"Battery\",\"b\":\"Up to 8.5 hours* - Supports Rapid Charge\"},{\"a\":\"Brand\",\"b\":\"ideapad\"},{\"a\":\"Touch Screen\",\"b\":\"Non-Touch\"},{\"a\":\"Weight\",\"b\":\"Starting at 1.4kg / 3.08lbs\"},{\"a\":\"ProductCondition\",\"b\":\"  New\"}],\"inventoryStatus\":2,\"isMedion\":null,\"isDCGSubseries\":false,\"isOutlet\":true,\"productCondition\":\"  New\",\"outletFlagColor\":\"#4C1984\",\"serialNumber\":\"\",\"serverFixedConfiguration\":null,\"productType\":0}],\"mainDetailDtoList\":[{\"partNumber\":\"81VU00D5US\",\"reviews\":{\"averageOverallRating\":\"4.4\",\"totalReviewCount\":355},\"leadTime\":\"Ships FREE Next Business Day\",\"productType\":0,\"customizable\":\"No\",\"readyToShip\":false}]}]}\n\n"
        expected {:keyboard "Traditional, Grey with Number Pad - English (US)",
                  :wlan "Intel® Wireless-AC 9560 2x2 AC",
                  :product-number "81VU00D5US",
                  :color "Ice Blue",
                  :memory "4GB DDR4 2400MHz soldered to systemboard",
                  :battery "Up to 8.5 hours* - Supports Rapid Charge",
                  :bluetooth "Bluetooth® 5.0 or above",
                  :brand "ideapad",
                  :warranty "1 Year Standard Depot Warranty",
                  :weight "Starting at 1.4kg / 3.08lbs",
                  :operating-system "Windows 11 Home in S mode, English",
                  :graphics "Integrated Intel UHD Graphics 600",
                  :url "/p/laptops/ideapad/ideapad-100/IdeaPad-1i-14IGL-5/81VU00D5US",
                  :pointing-device "ClickPad",
                  :camera "0.3MP with Dual Array Microphone",
                  :storage "128GB Solid State Drive M.2",
                  :display
                  "14\" FHD (1920 x 1080), TN, Anti-Glare, Non-Touch, 220 nits, LED Backlight, Narrow Bezel",
                  :inventory-status 2,
                  :product-mkt-name "IdeaPad 1 14IGL05 ",
                  :touch-screen "Non-Touch",
                  :product-condition "New",
                  :processor
                  "Intel Celeron N4020 Processor (2C / 2T, 1.1 / 2.8GHz, 4MB)",
                  :ac-adapter "45W"}]
    (is (= expected (o/extract-flatten-detail body))))

  (let [body "{\"xrid\":\"f49444ecc872a3b9512af562e685f0a4\",\"success\":true,\"status\":200,\"msg\":null,\"params\":\"-\",\"data\":[{\"categoryCode\":\"laptops\",\"categoryName\":\"Laptops\",\"categoryListImage\":\"//p1-ofp.static.pub/fes/cms/2021/08/18/pzieiyt3as8bzktep20ahz1qitn7ak694928.png\",\"productList\":[{\"merchandisingFlag\":null,\"listImage\":[{\"imageAddress\":\"//p4-ofp.static.pub/fes/cms/2022/03/18/0z0vsb5rveltkry7qu2je4xh0elvbl132979.png\",\"imageName\":\"22tpx13x3n1.png\",\"imageCaption\":null}],\"thumbnail\":null,\"productMktName\":\"ThinkPad X13 Intel (13”) - Black\",\"modelsNumber\":null,\"marketingStatus\":\"Available\",\"productNumber\":\"20T3X03500\",\"inventoryMessage\":\"Almost sold out, act now!\",\"url\":\"/p/laptops/thinkpad/thinkpadx/ThinkPad-X13-(Intel)-/20T3X03500\",\"classification\":[{\"a\":\"Processor\",\"b\":\"10th Generation Intel® Core™ i5-10210U Processor (1.60 GHz, up to 4.20 GHz with Turbo Boost, 4 Cores, 8 Threads, 6 MB Cache)\"},{\"a\":\"Operating System\",\"b\":\"<span style=\\\"white-space:nowrap;\\\">Windows 10 </span>Pro 64\"},{\"a\":\"Graphics\",\"b\":\"Integrated Intel® UHD Graphics\"},{\"a\":\"Memory\",\"b\":\"16 GB DDR4 2667MHz (Soldered)\"},{\"a\":\"Storage\",\"b\":\"128 GB PCIe SSD\"},{\"a\":\"Display\",\"b\":\"13.3\\\" HD (1366 x 768) anti-glare, 250 nits\"},{\"a\":\"Camera\",\"b\":\"720p HD\"},{\"a\":\"Fingerprint Reader\",\"b\":\"Fingerprint Reader\"},{\"a\":\"Keyboard\",\"b\":\"US - English\"},{\"a\":\"WLAN\",\"b\":\"Intel® Wi-Fi 6 AX201 802.11AX (2 x 2) & Bluetooth® 5.2\"},{\"a\":\"Warranty\",\"b\":\"1 Year Depot or Carry-in\"},{\"a\":\"Battery\",\"b\":\"Up to 15.9 hours (MM14) / Up to 10 hours (MM18)\"},{\"a\":\"Brand\",\"b\":\"ThinkPad\"},{\"a\":\"Touch Screen\",\"b\":\"Non-Touch\"},{\"a\":\"Weight\",\"b\":\"Starting at 2.84 lbs (1.29 kg)\"},{\"a\":\"ProductCondition\",\"b\":\"  Refurbished\"}],\"inventoryStatus\":1,\"isMedion\":null,\"isDCGSubseries\":null,\"isOutlet\":true,\"productCondition\":\"  Refurbished\",\"outletFlagColor\":\"#4C1984\",\"serialNumber\":\"\",\"serverFixedConfiguration\":null,\"productType\":0}],\"mainDetailDtoList\":[{\"partNumber\":\"20T3X03500\",\"reviews\":{\"averageOverallRating\":\"4.7\",\"totalReviewCount\":322},\"leadTime\":\"Ships FREE Next Business Day\",\"productType\":0,\"customizable\":\"No\",\"readyToShip\":false}]}]}\n\n"
        expected {:keyboard "US - English",
                  :wlan "Intel® Wi-Fi 6 AX201 802.11AX (2 x 2) & Bluetooth® 5.2",
                  :product-number "20T3X03500",
                  :memory "16 GB DDR4 2667MHz (Soldered)",
                  :battery "Up to 15.9 hours (MM14) / Up to 10 hours (MM18)",
                  :brand "ThinkPad",
                  :warranty "1 Year Depot or Carry-in",
                  :fingerprint-reader "Fingerprint Reader",
                  :weight "Starting at 2.84 lbs (1.29 kg)",
                  :operating-system "Windows 10 Pro 64",
                  :graphics "Integrated Intel® UHD Graphics",
                  :url "/p/laptops/thinkpad/thinkpadx/ThinkPad-X13-(Intel)-/20T3X03500",
                  :camera "720p HD",
                  :storage "128 GB PCIe SSD",
                  :display "13.3\" HD (1366 x 768) anti-glare, 250 nits",
                  :inventory-status 1,
                  :product-mkt-name "ThinkPad X13 Intel (13”) - Black",
                  :touch-screen "Non-Touch",
                  :product-condition "Refurbished",
                  :processor
                  "10th Generation Intel® Core™ i5-10210U Processor (1.60 GHz, up to 4.20 GHz with Turbo Boost, 4 Cores, 8 Threads, 6 MB Cache)"}]
    (is (= expected (o/extract-flatten-detail body)))))
