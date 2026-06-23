# DB Subnet Group (associates RDS to private subnets across multiple AZs)
resource "aws_db_subnet_group" "rds" {
  name       = "${var.project_name}-db-subnet-group"
  subnet_ids = [aws_subnet.private_1.id, aws_subnet.private_2.id]

  tags = {
    Name        = "${var.project_name}-rds-subnet-group"
    Environment = var.environment
  }
}

# Security Group for Database (allows access only from Application Containers)
resource "aws_security_group" "db" {
  name        = "${var.project_name}-db-sg"
  description = "Allow inbound PostgreSQL traffic from backend service"
  vpc_id      = aws_vpc.main.id

  # Will be populated with egress/ingress rules linking to ECS Security Group later

  tags = {
    Name        = "${var.project_name}-db-sg"
    Environment = var.environment
  }
}

# Amazon RDS PostgreSQL Database Instance
resource "aws_db_instance" "postgres" {
  identifier             = "${var.project_name}-db-${var.environment}"
  allocated_storage      = 20
  max_allocated_storage  = 100 # Auto-scaling storage enabled
  engine                 = "postgres"
  engine_version         = "15.4"
  instance_class         = "db.t4g.micro" # Cost-effective Graviton instance
  db_name                = "taskmanager"
  username               = "dbadmin"
  password               = var.db_password
  db_subnet_group_name   = aws_db_subnet_group.rds.name
  vpc_security_group_ids = [aws_security_group.db.id]
  skip_final_snapshot    = true
  multi_az               = false # Set to true in prod for high availability

  tags = {
    Name        = "${var.project_name}-db"
    Environment = var.environment
  }
}
