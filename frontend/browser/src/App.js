import React, { Component } from 'react';
import './App.css';
import {ThePlace} from './components/thePlace';


class App extends Component {
  render() {
    return (
      <div className="App">
        <ThePlace />
      </div>
    );
  }
}

export default App;
