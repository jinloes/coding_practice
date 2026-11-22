# Case study practice prompts

This deck turns the worked case studies into spoiler-free mock interviews. Read
only the base prompt at first. Reveal assumptions when the candidate asks a
relevant question, then introduce follow-ups one at a time.

The linked worked answers are for review after the attempt.

## Practice formats

### 15-minute lightning round

1. Read the base prompt.
2. Clarify scope and make one forcing estimate.
3. Draw the read and write paths.
4. Answer one deep-dive question.
5. Handle one failure injection.

### 45-minute mock interview

1. Spend 5 minutes clarifying requirements.
2. Spend 5 minutes estimating scale.
3. Spend 15 minutes on API, data model, and high-level design.
4. Spend 10 minutes on one deep dive.
5. Spend 10 minutes on failures, trade-offs, and the next bottleneck.

The interviewer should not read every question. Pick the follow-up that probes
the weakest or most important part of the candidate's design.

## 1. URL shortener

### Base prompt

> Design a service that creates short URLs and redirects visitors to the
> original destinations. Users may optionally choose a custom alias and an
> expiration time.

### Reveal if asked

- The service creates 1 million links and handles 100 million redirects per
  day.
- Redirect p99 should remain under 100 ms.
- Links normally remain for five years.
- A newly created link must work immediately.
- Click analytics can be asynchronous.

### Clarification and estimation questions

1. Which operations and user roles are in scope?
2. What read/write ratio drives the design?
3. How much metadata accumulates over the retention period?
4. Does a destination ever change after a code is created?
5. Should redirects use a temporary or permanent HTTP status?

### Design and deep-dive questions

1. How are short codes generated without collisions?
2. What changes when aliases must be globally unique and human-selected?
3. Which database key and partition key would you choose?
4. Where would you cache redirects, and how would expiration work?
5. How does creation provide immediate read-after-write behavior?
6. How do analytics avoid increasing redirect latency?
7. What protects the system when one link becomes globally viral?

### Failure injections

- The distributed cache becomes unavailable during peak traffic. Walk through
  how the database is protected.
- A creation response is lost after the database commit. What does the client
  retry do?
- A malicious link must stop redirecting within 30 seconds even though it is
  cached worldwide.
- Attackers send random codes at 100 times normal traffic.

[Review the worked URL shortener](url-shortener.md) after completing the drill.

## 2. Distributed API rate limiter

### Base prompt

> Design a distributed rate limiter for API gateways. Policies can limit a
> tenant, user, credential, IP address, endpoint, or combinations of them.

### Reveal if asked

- Gateways handle 1 million requests per second globally.
- A decision should add less than 5 ms p99 latency within a region.
- There can be 10 million active limit keys.
- Policies define a sustained rate and an allowed burst.
- Small bounded overshoot is acceptable for ordinary API traffic.
- Expensive mutations may fail closed; low-risk reads may degrade locally.

### Clarification and estimation questions

1. Is the product limiting rate, concurrency, long-term quota, or all three?
2. How strict must a global limit be?
3. Which identities can the gateway trust?
4. How quickly must policy updates take effect?
5. What state size and operation rate must the limiter support?

### Design and deep-dive questions

1. Which limiting algorithm would you use, and why?
2. Where does the authoritative token state live?
3. How is one bucket updated atomically across many gateways?
4. How would you avoid a hot key for a global service limit?
5. How do hierarchical limits apply to one request?
6. How does shard rebalancing avoid temporarily doubling an allowance?
7. How are policy versions distributed and observed in decisions?

### Failure injections

- A gateway cannot reach the limiter for 20 seconds. Which requests proceed?
- One tenant generates 20% of all traffic and saturates its bucket shard.
- A policy rollout accidentally cuts every limit by 90%.
- Backend saturation requires rejecting requests whose rate buckets still have
  tokens.

[Review the worked API rate limiter](api-rate-limiter.md) after completing the
drill.

## 3. Notification platform

### Base prompt

> Design a platform that accepts notification requests from internal services
> and delivers email, SMS, and mobile push while honoring user preferences.

### Reveal if asked

- The system performs 500 million channel deliveries per day and peaks near
  60,000 deliveries per second.
- Transactional notifications should start within five seconds.
- Bulk campaigns may take minutes but cannot delay transactional traffic.
- Provider callbacks can be duplicated, delayed, or out of order.
- Accepted requests must not disappear.

### Clarification and estimation questions

1. Is campaign audience selection in scope, or does each request identify a
   recipient?
2. Which notification categories may override preferences?
3. What does "delivered" mean for each provider?
4. Is ordering required across channels or only within a category?
5. Which sensitive recipient data may appear in events and logs?

### Design and deep-dive questions

1. What happens between API acceptance and provider submission?
2. How are user preferences and template versions resolved reproducibly?
3. How do queues prevent a bulk SMS campaign from delaying password-reset
   email?
4. What makes provider retries safe?
5. How are scheduled notifications stored until they become due?
6. How are tenant fairness and provider quotas enforced together?
7. How would a new provider be introduced without changing every caller?

### Failure injections

- An email provider times out after possibly accepting the request.
- A template version causes every worker to fail.
- The preference service is unavailable during a marketing campaign.
- A provider sends the same delivered webhook five times.

[Review the worked notification platform](notification-platform.md) after
completing the drill.

## 4. Realtime chat

### Base prompt

> Design a realtime chat service for one-to-one conversations and groups of up
> to 500 members. Support multiple devices, message history, delivery receipts,
> and read receipts.

### Reveal if asked

- There are 50 million daily users and 5 million simultaneous connections.
- The system handles 1 billion messages per day and peaks near 120,000
  messages per second.
- Online delivery should normally complete within 500 ms.
- Messages in one conversation require a stable server order.
- Presence can be approximate and one minute stale.
- Accepted messages must survive a zone failure.

### Clarification and estimation questions

1. Are public broadcast channels or only bounded groups required?
2. Is end-to-end encryption in scope?
3. How long is message history retained?
4. What acknowledgment stages should the client display?
5. What connection and heartbeat load does the service handle?

### Design and deep-dive questions

1. Which tier owns persistent connections, and which tier owns durable state?
2. How is a single order assigned to concurrent messages in one conversation?
3. What partition key supports writes and history reads?
4. How does a reconnecting device switch from history to live delivery without
   missing a message?
5. How are duplicate sends and duplicate delivery events handled?
6. How are read receipts represented without one database write per message?
7. What changes when participants are in several regions?

### Failure injections

- A connection gateway holding 50,000 sockets fails.
- The client retries a message after losing the accepted acknowledgment.
- A recipient's mobile connection consumes messages much slower than they are
  produced.
- The conversation's home region fails after some replicas have lagged.

[Review the worked chat system](chat-system.md) after completing the drill.

## 5. Home news feed

### Base prompt

> Design a home news feed where users publish posts, follow accounts, and read
> a reverse-chronological feed.

### Reveal if asked

- There are 100 million daily users.
- The service receives 5 million posts and 2 billion feed reads per day.
- A typical user follows 300 accounts.
- Feed reads target p99 under 300 ms.
- New posts may take five seconds to appear in home feeds.
- Follower counts are highly skewed.

### Clarification and estimation questions

1. Is ranking chronological or personalized?
2. Should old posts appear when a user follows a new account?
3. How quickly must deletes, blocks, and privacy changes propagate?
4. Which operation needs read-after-write consistency?
5. What write amplification follows from the average audience size?

### Design and deep-dive questions

1. Would you fan out posts on write, on read, or use a hybrid?
2. How are high-follower accounts treated differently?
3. Which keys partition posts, follow edges, and materialized inboxes?
4. How does the feed paginate while new posts arrive?
5. How are duplicate fan-out events made harmless?
6. How are post bodies hydrated without copying them into every inbox?
7. What metrics show that feed freshness is about to miss its objective?

### Failure injections

- Fan-out workers stop for one hour and then restart.
- One account gains 100 million followers overnight.
- The feed cache fails and all first-page reads miss.
- A private post becomes public and then private again while events are delayed.

[Review the worked news feed](news-feed.md) after completing the drill.

## 6. Video upload and streaming

### Base prompt

> Design a platform where users upload prerecorded videos and viewers stream
> them at several resolutions on web and mobile clients.

### Reveal if asked

- The platform receives 100,000 uploads per day averaging 1 GB each.
- It serves 100 million playback sessions per day.
- Average viewing is five minutes at 3 Mbps.
- Playback should start within two seconds at p99.
- Uploads should resume after interruption.
- Most videos should become playable within ten minutes.

### Clarification and estimation questions

1. Are live streaming, recommendations, and comments in scope?
2. Which source and derived media must be retained?
3. How much daily ingest, storage, processing, and egress does the system imply?
4. When is a video considered published?
5. How urgent must moderation and takedown propagation be?

### Design and deep-dive questions

1. Why should application servers not proxy upload and playback bytes?
2. How does a resumable multipart upload become a durable processing request?
3. How are transcoding steps retried without creating conflicting outputs?
4. How does the encoding ladder support adaptive bitrate playback?
5. Which objects are cached at the CDN, and how are keys versioned?
6. How do you prevent a viral cache miss from overwhelming object storage?
7. How would processing capacity be shared fairly across videos and tenants?

### Failure injections

- A transcode worker crashes halfway through a two-hour video.
- One malformed source crashes every worker that attempts it.
- A new release goes viral before most CDN locations have cached it.
- A legal takedown must block playback immediately despite long CDN TTLs.

[Review the worked video platform](video-platform.md) after completing the
drill.

## 7. Payment processing

### Base prompt

> Design a payment service that lets merchants authorize and capture card
> payments through external providers, issue refunds, query status, and receive
> payment events.

### Reveal if asked

- The service receives 50 million payment attempts per day and peaks around
  6,000 attempts per second.
- The same logical payment must never be captured twice.
- Provider webhooks may be late, duplicated, or out of order.
- A provider may time out after accepting an operation.
- Every balance-changing outcome must be auditable and reconcilable.
- Raw card numbers are handled by a separate tokenization provider.

### Clarification and estimation questions

1. Which payment lifecycle steps are in scope?
2. What does the API return when final provider status is unknown?
3. Can one payment be partially captured or partially refunded?
4. Which system is authoritative for internal payment state?
5. What availability trade-off is acceptable when correctness is uncertain?

### Design and deep-dive questions

1. How does API idempotency survive a lost response?
2. What explicit payment states and legal transitions are required?
3. How is a provider idempotency key related to the internal attempt?
4. What happens when a provider request times out?
5. How are ledger entries written and balanced?
6. How do concurrent refunds avoid exceeding the captured amount?
7. How are webhooks authenticated, deduplicated, and ordered?
8. What does reconciliation compare, and how are discrepancies repaired?

### Failure injections

- The provider times out during capture and later sends a success webhook.
- An old failure webhook arrives after the payment is captured.
- A workflow worker crashes after the provider accepts the charge.
- The primary provider is down while several attempts have unknown status.
- A ledger write fails after a provider reports capture success.

[Review the worked payment service](payment-processing.md) after completing the
drill.

## 8. Ride-hailing dispatch

### Base prompt

> Design the dispatch portion of a ride-hailing service. Drivers publish
> location and availability, riders request trips, and the system finds and
> assigns a nearby driver.

### Reveal if asked

- The platform completes 10 million trips per day and peaks near 2,000 trip
  starts per second.
- Up to 2 million drivers are online and update location every four seconds.
- Candidate locations should be no more than ten seconds old.
- Riders should normally receive a match within five seconds.
- A driver must never be assigned to two active rides.
- Matching and assignment are owned by one operating region.

### Clarification and estimation questions

1. Are pricing, navigation, payments, and scheduled rides in scope?
2. How fresh must location be for matching versus map display?
3. May offers be sent to several drivers concurrently?
4. What trip state must survive a mobile disconnect?
5. Which traffic dominates: ride requests or location updates?

### Design and deep-dive questions

1. How is high-rate location state separated from durable trip state?
2. Which geospatial index supports nearby-driver search?
3. How does the search expand in dense and sparse areas?
4. What record is authoritative for driver availability?
5. How do concurrent offer acceptances result in only one matched driver?
6. What does an assignment epoch or fencing token protect?
7. How are matching retries resumed after a worker crash?
8. How are city hotspots and regional ownership handled?

### Failure injections

- Location ingestion is five minutes behind while the geo index still answers.
- Two drivers accept offers for the same ride at the same instant.
- A matcher crashes after reserving a driver but before updating the ride.
- The ETA service is unavailable during peak demand.
- A region loses quorum while drivers and riders remain connected.

[Review the worked ride-hailing dispatch](ride-hailing.md) after completing the
drill.

## Random constraint deck

After finishing a design, choose one constraint without changing the original
product scope:

1. Traffic grows by 10x, but the budget only doubles.
2. One tenant, user, or key produces half of all load.
3. A dependency accepts work and then times out.
4. One availability zone fails during peak traffic.
5. The primary region becomes unavailable for 30 minutes.
6. Read-after-write consistency becomes mandatory for one operation.
7. Data for one geography cannot leave that geography.
8. Queue consumers fall two hours behind.
9. The cache hit ratio drops from 95% to 20%.
10. A privacy deletion must propagate through every derived view.

For each constraint, state:

- Which requirement changed.
- Which component fails first.
- Which pattern or capacity change addresses it.
- What new trade-off the change introduces.
