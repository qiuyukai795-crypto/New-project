const API_BASE = import.meta.env.VITE_API_BASE_URL || "/api";

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...(options.headers || {})
    },
    ...options
  });

  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || "请求失败");
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
