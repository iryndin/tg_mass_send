 #!/bin/bash

echo "Going to start PostgreSQL docker image with 'tgms' database..."
docker run -d -p 5433:5432 \
  -e "POSTGRES_USER=tgmsapp" \
  -e "POSTGRES_PASSWORD=test123" \
  -e "POSTGRES_DB=tgms" \
  --name tgmsdb postgres:11

#
# run psql:
# docker exec -it tgmsdb psql -d tgms -U tgmsapp -W
#