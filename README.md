![logo](./pouchie-bino.svg)

# logaze-scraper

This is the scraper component of logaze, a webapp that presents a data-centric view of the laptops available on Lenovo outlet

`logaze` allows you to filter and sort through all the laptops at Lenovo outlet to get the one you want.

Use logaze at https://ackerleytng.github.io/logaze/!

## Components/Architecture

This app has three parts

+ Scraper (this repo)
+ [Frontend](https://github.com/ackerleytng/logaze)
+ Result cache, hosted at [jsonblob.com](https://jsonblob.com/)

See details in `logaze`'s README at https://github.com/ackerleytng/logaze.

## Development

> Please let me know if you want to contribute! I'll be so happy to work with you!

I used the repl a lot in the development of logaze, so my workflow is mostly emacs+cider.

You should be able to start the repl with cider, out of the box.

To start a development server with a browser, (this will trigger scraping!)

```
lein ring server
```

The browser tab that pops up will connect to localhost and trigger the scraping.

This will start the server without the browser.

```
lein ring server-headless
```

## Tests

```
lein test
```

## Deploying

```
lein ring uberjar
docker build -t quay.io/ackerleytng/logaze-scraper:0.1.0
docker push quay.io/ackerleytng/logaze-scraper:0.1.0
```

Login at koyeb.com and update (may need to bump the version numbers above)
