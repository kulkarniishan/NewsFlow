from flask import Flask, request, jsonify
from datasets import Dataset
from transformers import pipeline
import torch
import logging
import time

# === Logger Setup ===
class TZFormatter(logging.Formatter):
    def formatTime(self, record, datefmt=None):
        tz_offset = time.strftime('%z')
        record.z = tz_offset
        return super().formatTime(record, datefmt)

formatter = TZFormatter("[%(asctime)s %(z)s] [%(process)d] [%(levelname)s] %(message)s", datefmt="%Y-%m-%d %H:%M:%S")
handler = logging.StreamHandler()
handler.setFormatter(formatter)

logger = logging.getLogger()
logger.setLevel(logging.INFO)
logger.handlers = []
logger.addHandler(handler)

# === CUDA & NER Setup ===
cuda_available = torch.cuda.is_available()
device_id = 0 if cuda_available else -1

logger.info("CUDA available: %s", cuda_available)
if cuda_available:
    logger.info("Using GPU: %s", torch.cuda.get_device_name(0))
else:
    logger.info("Using CPU")

ner_pipeline = pipeline("ner", model="dslim/bert-base-NER", aggregation_strategy="simple", device=device_id, batch_size=8)

# === Flask App ===
app = Flask(__name__)

def format_entities(entities):
    return [
        {
            "text": ent["word"],
            "label": ent["entity_group"],
            "score": float(round(float(ent["score"]), 3))
        }
        for ent in entities
    ]

@app.route("/enrich", methods=["POST"])
def enrich():
    data = request.get_json()

    if not data:
        logger.warning("No JSON payload received")
        return jsonify({"error": "Missing JSON payload"}), 400

    # Handle single text
    if "text" in data:
        text = data["text"]
        if not isinstance(text, str) or not text.strip():
            logger.warning("Invalid 'text' field")
            return jsonify({"error": "'text' must be a non-empty string"}), 400

        try:
            logger.info("Received single text for enrichment")
            result = ner_pipeline(text)
            formatted = format_entities(result)
            logger.info("Successfully enriched single text")
            return jsonify({"entities": formatted})

        except Exception as e:
            logger.exception("Error during single-text enrichment")
            return jsonify({"error": str(e)}), 500

    # Handle batch texts
    elif "texts" in data:
        texts = data["texts"]
        if not isinstance(texts, list) or not all(isinstance(t, str) for t in texts):
            logger.warning("Invalid 'texts' field")
            return jsonify({"error": "'texts' must be a list of strings"}), 400

        try:
            logger.info("Received %d texts for enrichment", len(texts))
            # logger.info("Texts to process: %s", texts)
            ds = Dataset.from_dict({"text": texts})
            logger.info("Dataset text column: %s", ds["text"])
            # Process with NER pipeline
            results = ner_pipeline(texts)

            batch_entities = []

            for text, result in zip(texts, results):
                formatted = format_entities(result)
                batch_entities.append({"text": text, "entities": formatted})

            logger.info("Successfully enriched batch")
            return jsonify({"batch_entities": batch_entities})

        except Exception as e:
            logger.exception("Error during batch-text enrichment")
            return jsonify({"error": str(e)}), 500

    else:
        logger.warning("Missing 'text' or 'texts' field in request")
        return jsonify({"error": "Missing 'text' or 'texts' field"}), 400

if __name__ == "__main__":
    logger.info("Starting Flask NER enrichment server...")
    app.run(host="0.0.0.0", port=5000)
