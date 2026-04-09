import { reactive } from "vue";
import {
  AUTH_TOKEN_STORAGE_KEY,
  fetchAuthProviders,
  fetchCurrentUser
} from "./api";

const LOGIN_REDIRECT_STORAGE_KEY = "dianping_login_redirect";

export const authState = reactive({
  token: localStorage.getItem(AUTH_TOKEN_STORAGE_KEY),
  user: null,
  providers: [],
  providersLoading: false,
  initialized: false
});

let providersPromise = null;

export async function bootstrapAuth() {
  if (authState.initialized) {
    return;
  }

  try {
    await loadAuthProviders(true);
  } catch {
    authState.providers = [];
  }
  authState.initialized = true;

  if (!authState.token) {
    return;
  }

  try {
    await loadCurrentUser();
  } catch {
    clearAuthSession();
  }
}

export async function loadAuthProviders(force = false) {
  if (!force && authState.providers.length) {
    return authState.providers;
  }

  if (providersPromise) {
    return providersPromise;
  }

  authState.providersLoading = true;
  providersPromise = fetchAuthProviders()
    .then((providers) => {
      authState.providers = Array.isArray(providers) ? providers : [];
      return authState.providers;
    })
    .finally(() => {
      authState.providersLoading = false;
      providersPromise = null;
    });

  return providersPromise;
}

export async function refreshAuthProviders() {
  try {
    return await loadAuthProviders(true);
  } catch {
    authState.providers = [];
    return [];
  }
}

export async function loadCurrentUser() {
  if (!authState.token) {
    authState.user = null;
    return null;
  }

  authState.user = await fetchCurrentUser();
  return authState.user;
}

export function setAccessToken(token) {
  authState.token = token;
  localStorage.setItem(AUTH_TOKEN_STORAGE_KEY, token);
}

export function rememberLoginRedirectPath(redirectPath = "/") {
  sessionStorage.setItem(LOGIN_REDIRECT_STORAGE_KEY, redirectPath);
}

export function peekLoginRedirectPath() {
  return sessionStorage.getItem(LOGIN_REDIRECT_STORAGE_KEY) || "/";
}

export function clearAuthSession() {
  authState.token = null;
  authState.user = null;
  localStorage.removeItem(AUTH_TOKEN_STORAGE_KEY);
}

export function startOAuthLogin(provider, redirectPath = "/") {
  rememberLoginRedirectPath(redirectPath);
  window.location.assign(provider.authorizationUrl);
}

export function isPasswordProvider(provider) {
  return provider?.providerType === "password";
}

export function consumeLoginRedirectPath() {
  const redirectPath = sessionStorage.getItem(LOGIN_REDIRECT_STORAGE_KEY) || "/";
  sessionStorage.removeItem(LOGIN_REDIRECT_STORAGE_KEY);
  return redirectPath;
}
