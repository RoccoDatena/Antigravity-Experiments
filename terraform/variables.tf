variable "aws_region" {
  description = "AWS region to deploy resources"
  type        = string
  default     = "eu-west-1" # Ireland, default region
}

variable "environment" {
  description = "Deployment environment name"
  type        = string
  default     = "production"
}

variable "project_name" {
  description = "Project name prefix for resources"
  type        = string
  default     = "task-manager"
}

variable "db_password" {
  description = "Password for the RDS PostgreSQL database"
  type        = string
  sensitive   = true
  default     = "SuperSecretPGPassword123!" # In real production, use Secrets Manager
}
