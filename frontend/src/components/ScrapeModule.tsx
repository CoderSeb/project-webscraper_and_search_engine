import { useState } from 'react'

enum Scrape {
  Cryptocurrency = 'Cryptocurrency',
  Golf = 'Golf',
  Sweden = 'Sweden',
  Artificial_intelligence = 'Artificial_intelligence'
}

function ScrapeModule () {
  const [scrape, setScrape] = useState<Scrape>(Scrape.Cryptocurrency)

  const handleScrape = () => {
    fetch(`http://localhost:8080/api/scrape?entry=${scrape}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
    })
  }


  return (
    <div>
      <select onChange={e => setScrape(e.target.value as Scrape)}>
        <option value={Scrape.Cryptocurrency}>{Scrape.Cryptocurrency}</option>
        <option value={Scrape.Golf}>{Scrape.Golf}</option>
        <option value={Scrape.Sweden}>{Scrape.Sweden}</option>
        <option value={Scrape.Artificial_intelligence}>{Scrape.Artificial_intelligence}</option>
      </select>
    </div>
  )
}

export default ScrapeModule