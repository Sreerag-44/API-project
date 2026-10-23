import http from 'k6/http';
import { Counter } from 'k6/metrics';

// Custom metrics to track the exact requirements
export const storedCount = new Counter('stored_submissions_201');
export const duplicateCount = new Counter('duplicate_submissions_200');
export const errorCount = new Counter('total_errors');

export const options = {
  // Use a shared pool of 100,000 total iterations across concurrent workers
  scenarios: {
    submission_test: {
      executor: 'shared-iterations',
      vus: 50,                  // 50 concurrent virtual users
      iterations: 100000,       // Exactly 100,000 requests
      maxDuration: '10m',       // Timeout safety
    },
  },
};

const TOTAL_UNIQUE = 80000;

export default function () {
  // __ITER ranges from 0 to 99,999
  const iteration = __ITER;

  let idempotencyKey;
  if (iteration < TOTAL_UNIQUE) {
    // First 80,000 requests get a unique key
    idempotencyKey = `key-${iteration}`;
  } else {
    // Remaining 20,000 pick randomly from the first 80,000 keys
    const reusedIndex = Math.floor(Math.random() * TOTAL_UNIQUE);
    idempotencyKey = `key-${reusedIndex}`;
  }

  const payload = JSON.stringify({
    form_id: 'contact-form',
    idempotency_key: idempotencyKey,
    email: 'user@example.com',
    data: { name: 'LoadTester', message: 'Stress test ping' },
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  const res = http.post('http://localhost:8080/submissions', payload, params);

  // Classify response
  if (res.status === 201) {
    storedCount.add(1);
  } else if (res.status === 200) {
    duplicateCount.add(1);
  } else {
    errorCount.add(1);
  }
}
