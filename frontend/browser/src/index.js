import 'babel-polyfill'
import thunkMiddleware from 'redux-thunk';
import { createLogger } from 'redux-logger';
import React from 'react';
import ReactDOM from 'react-dom';
import App from './App';
import {Provider} from 'react-redux';
import {createStore, applyMiddleware} from 'redux';
import {loadPlace, loadTimeout} from './actions';
import {init as websocketInit} from './actions/websocket';
import rootReducer from  './reducers';
import './index.css';

const loggerMiddleware = createLogger();

const store = createStore(
    rootReducer,
    applyMiddleware(
        thunkMiddleware,
        loggerMiddleware
    )
);

websocketInit(store);
store.dispatch(loadTimeout());
store.dispatch(loadPlace());

ReactDOM.render(
    <Provider store={store}>
        <App />
    </Provider>,
  document.getElementById('root')
);
