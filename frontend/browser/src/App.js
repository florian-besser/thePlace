import React, { Component } from 'react';
import './App.css';
import {ThePlace} from './components/thePlace';


class App extends Component {
  render() {
    return (
      <div className="App">
        <div className="App-header">
          <h2>The Place</h2>
        </div>
        <ThePlace />
      </div>
    );
  }
}

export default App;
