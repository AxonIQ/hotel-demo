/*
 * Copyright (c) 2020-2020. AxonIQ
 *
 * Licensed under the Apache License, Version 2.0 (the &quot;License&quot;)
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

const defaultHeaders = new Headers();
defaultHeaders.append("Content-Type", "application/json");

const defaultOptions: RequestInit = {
  cache: "no-cache",
  credentials: "same-origin",
  headers: defaultHeaders,
};

type ResponseInterceptor = (response: Response) => Response;

async function handleRequest(
  url: string,
  options: RequestInit,
  responseInterceptor: ResponseInterceptor
) {
  const response = await fetch(url, options);
  responseInterceptor(response);

  if (!response.ok) {
    const contentType = response.headers.get("content-type");
    if (contentType && contentType.indexOf("application/json") !== -1) {
      const errorObject = await response.json();
      throw errorObject;
    } else {
      const error = await response.text();
      throw error;
    }
  }

  return response;
}

export const fetchWrapper = {
  responseInterceptor(response: Response) {
    return response;
  },

  setAuthorizationToken(authToken: string) {
    // Clean out any authorization headers, before setting this one
    this.removeAuthorizationToken();
    defaultHeaders.append("Authorization", `Bearer ${authToken}`);
  },
  removeAuthorizationToken() {
    defaultHeaders.delete("Authorization");
  },

  async get(url: string, customOptions?: RequestInit) {
    const options = {
      ...defaultOptions,
      method: "GET",
      ...customOptions,
    };
    return handleRequest(url, options, this.responseInterceptor);
  },
  async post(url: string, body?: any, customOptions?: RequestInit) {
    const options: RequestInit = {
      ...defaultOptions,
      method: "POST",
      body: JSON.stringify(body),
      ...customOptions,
    };
    return handleRequest(url, options, this.responseInterceptor);
  },

  async put(url: string, body?: any, customOptions?: RequestInit) {
    const options = {
      ...defaultOptions,
      method: "PUT",
      body: JSON.stringify(body),
      ...customOptions,
    };
    return handleRequest(url, options, this.responseInterceptor);
  },

  async patch(url: string, body?: any, customOptions?: RequestInit) {
    const options = {
      ...defaultOptions,
      method: "PATCH",
      body: JSON.stringify(body),
      ...customOptions,
    };
    return handleRequest(url, options, this.responseInterceptor);
  },

  // We use this because `delete` is a reserved word.
  async remove(url: string, customOptions?: RequestInit) {
    const options = {
      ...defaultOptions,
      method: "DELETE",
      ...customOptions,
    };
    return handleRequest(url, options, this.responseInterceptor);
  },
};
