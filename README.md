# Vert.x Pipeline Architecture

A reactive event-driven order processing system using Vert.x with:
- Linear pipeline workflow
- Automatic correlation ID propagation
- Immutable message passing
- Isolated business logic components

## Key Features

🚀 **Pipeline Architecture**
- Chained verticles for clear workflow (Order → Inventory → Payment → Confirmation)
- Each verticle handles one specific business capability

🔍 **Built-in Observability**
- Auto-generated correlation IDs for all transactions
- MDC logging context for traceability
- Time-history tracking in messages

🧩 **Modular Design**
- Business logic isolated from infrastructure code
- Verticles communicate via immutable messages
- Easy to add/remove processing steps

## Getting Started

### Prerequisites
- Java 11+
- Maven 3.6+
- Vert.x 4.3+

### Installation
```bash
git clone https://github.com/Glad1980/event-driven-pipeline.git
cd event-driven-pipeline
mvn clean install