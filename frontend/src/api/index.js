import axios from 'axios';

axios.defaults.baseURL = '/api/v1';

export * as eveningsAPI from './eveningsApi';
export * as reportsAPI from './reportsApi';
export * as expensesAPI from './expensesApi';
export * as earningsAPI from './earningsApi';
