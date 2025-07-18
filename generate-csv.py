#!/usr/bin/env python3
import csv
import random
import argparse
import os
import time
random.seed(time.time())

def generate_csv(num_rows: int, output_path: str):
    """
    Generates a CSV file with random customer data.

    :param num_rows: Number of data rows to generate (excluding header).
    :param output_path: Path for the generated CSV file.
    """
    first_names = [
        "John", "Jane", "Alice", "Bob", "Charlie",
        "David", "Emma", "Frank", "Grace", "Helen"
    ]
    last_names = [
        "Doe", "Smith", "Johnson", "Wang", "Brown",
        "Lee", "Kim", "Martin", "Cho", "Park"
    ]
    domains = [
        "example.com", "test.com", "another.com",
        "sample.com", "test.org"
    ]

    # Ensure output directory exists
    os.makedirs(os.path.dirname(output_path), exist_ok=True)

    with open(output_path, 'w', newline='', encoding='utf-8') as f:
        writer = csv.writer(f)
        # 헤더 작성
        writer.writerow(['id', 'firstName', 'lastName', 'email'])
        # 데이터 로우 작성
        for i in range(1, num_rows + 1):
            first = random.choice(first_names)
            last = random.choice(last_names)
            domain = random.choice(domains)
            email = f"{first.lower()}.{last.lower()}@{domain}"
            writer.writerow([i, first, last, email])

def main():
    parser = argparse.ArgumentParser(
        description="Generate a random customers CSV file."
    )
    parser.add_argument(
        '-n', '--num-rows',
        type=int,
        default=10,
        help="Number of rows to generate (default: 10)"
    )
    parser.add_argument(
        '-o', '--output',
        type=str,
        default='customers.csv',
        help="Output CSV file path (default: customers.csv)"
    )
    args = parser.parse_args()

    generate_csv(args.num_rows, args.output)
    print(f"Generated '{args.output}' with {args.num_rows} rows.")

if __name__ == '__main__':
    main()