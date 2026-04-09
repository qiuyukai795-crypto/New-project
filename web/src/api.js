export const AUTH_TOKEN_STORAGE_KEY = "dianping_access_token";
const API_BASE = import.meta.env.VITE_API_BASE_URL || "/api";

async function request(path, options = {}) {
  const token = localStorage.getItem(AUTH_TOKEN_STORAGE_KEY);
  const response = await fetch(`${API_BASE}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(options.headers || {})
    },
    ...options
  });

  if (!response.ok) {
    const contentType = response.headers.get("content-type") || "";
    const payload = contentType.includes("application/json")
      ? await response.json().catch(() => null)
      : await response.text();

    if (response.status === 401) {
      throw new Error("请先登录后再操作。");
    }

    if (typeof payload === "string") {
      throw new Error(payload || "请求失败");
    }

    throw new Error(payload?.message || payload?.error || "请求失败");
  }

  return response.json();
}

export function fetchShops(keyword = "") {
  const query = keyword ? `?keyword=${encodeURIComponent(keyword)}` : "";
  return request(`/shops${query}`);
}

export function fetchShop(id) {
  return request(`/shops/${id}`);
}

export function fetchReviews(id) {
  return request(`/shops/${id}/reviews`);
}

export function fetchRecommendations(id) {
  return request(`/shops/${id}/recommendations`);
}

export function createReview(id, payload) {
  return request(`/shops/${id}/reviews`, {
    method: "POST",
    body: JSON.stringify(payload)
  });
}

export function fetchAuthProviders() {
  return request("/auth/providers");
}

export function fetchCurrentUser() {
  return request("/auth/me");
}
