import { useState } from 'react'
import Button from 'react-bootstrap/Button'
import Form from 'react-bootstrap/Form'
import Table from 'react-bootstrap/Table'
import './App.css'

interface IFullResult {
  numberOfResults: number
  duration: number
  results: IResult[]
}

interface IResult {
  link: string
  score: number
  content: number
  location: number
  pageRank: number
}

function App() {
  const [results, setResults] = useState<IFullResult>()
  const [searchQuery, setSearchQuery] = useState('')

  const handleSearch = () => {
    setResults(undefined)
    fetch(`http://localhost:8080/api/search`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        query: searchQuery
      })
    })
      .then((res) => res.json())
      .then((result: IFullResult) => {
        setResults(result)
      })
  }
  return (
    <div className='App'>
      <h1>Project - Webscraper/SearchEngine</h1>
      <div className='searchContainer'>
        <div className='searchInputContainer'>
          <Form.Control
            className='searchInput'
            type='text'
            defaultValue={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder='Search...'
          />
          <Button
            className='searchButton'
            variant='dark'
            onClick={handleSearch}>
            Search!
          </Button>
        </div>

        <div className='searchResultContainer'>
          <Table striped bordered hover variant='dark'>
            <thead>
              <tr>
                <th className='linkField'>Link</th>
                <th className='scoreField'>Score</th>
                <th className='contentField'>Content</th>
                <th className='locationField'>Location</th>
                <th className='pageRankField'>Page Rank</th>
              </tr>
            </thead>
            <tbody>
              {results &&
                results.results.map((result, index) => (
                  <tr key={index}>
                    <td className='linkField'><a href={`https://en.wikipedia.org/wiki/${result.link}`} target="_blank">{result.link}</a></td>
                    <td className='scoreField'>{result.score.toFixed(2)}</td>
                    <td className='contentField'>
                      {result.content.toFixed(2)}
                    </td>
                    <td className='locationField'>
                      {result.location.toFixed(2)}
                    </td>
                    <td className='pageRankField'>
                      {result.pageRank.toFixed(2)}
                    </td>
                  </tr>
                ))}
            </tbody>
          </Table>
          {results && (
            <p className='resultCount'>
              Found {results.numberOfResults} results in {results.duration}{' '}
              seconds
            </p>
          )}
        </div>
      </div>
    </div>
  )
}

export default App
